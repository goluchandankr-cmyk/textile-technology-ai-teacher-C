package com.example.data.api

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = 0.4f,
    val topP: Float? = 0.9f
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

interface GeminiApiEndpoint {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiEndpoint by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiEndpoint::class.java)
    }
}

fun Bitmap.toBase64Jpeg(): String {
    val outputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}

class GeminiTextileService {

    private val systemPrompt = """
        You are an experienced, empathetic Textile Technology & Engineering Teacher for Diploma students.
        Your goal is to explain Textile Technology concepts in a simple, clear, exam-oriented manner.
        
        Answer Guidelines:
        1. Target Audience: Diploma in Textile Technology / Textile Engineering students.
        2. Language: Use simple Hindi mixed with English Technical Terms (e.g. Hinglish or Hindi with English terms in brackets). Always present technical terms clearly in English (e.g., Opening, Cleaning, Winding, Rapier Loom, Uster Tester).
        3. Structure according to Question Type:
           - Definition Question: Simple exam-worthy Definition + Key Summary.
           - Short Question: 3 to 6 key bullet points + a boxed "Exam Point" summary.
           - Long Question: Headings -> 1. Introduction, 2. Definition, 3. Principle, 4. Construction/Parts, 5. Working, 6. Advantages, 7. Disadvantages, 8. Applications, 9. Conclusion.
           - Difference Question: Clear Comparison Table format (Basis | Machine A | Machine B).
           - Machine Question: Machine Name, Definition, Principle, Main Parts & Functions, Working, Pros/Cons, Specifications, Exam Points.
           - Practical Question: Experiment No, Aim/Objective, Apparatus Required, Theory/Principle, Construction, Working Procedure, Observation, Calculation (with formula & units), Result, Precautions, Viva Questions.
        4. If mathematical/numerical, show step-by-step calculations with exact formulas and proper units (Tex, Ne, Denier, Picks/inch, Ends/inch, Efficiency %).
        5. Provide concise Exam Points for quick revision before exams.
    """.trimIndent()

    suspend fun askTextileQuestion(
        prompt: String,
        language: String = "HINDI_ENGLISH",
        questionTypeFormat: String = "AUTO",
        imageBitmap: Bitmap? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineFallbackAnswer(prompt, questionTypeFormat)
        }

        val langInstruction = when (language) {
            "ENGLISH" -> "Answer in simple, clean English with technical accuracy."
            "HINGLISH" -> "Answer in natural Hinglish (Hindi written in Roman/English script + Technical English terms)."
            else -> "Answer in simple Hindi with English Technical Terms shown clearly."
        }

        val formatInstruction = when (questionTypeFormat) {
            "DEFINITION" -> "Format as a crisp Definition Question with Exam Point."
            "SHORT" -> "Format as a Short Question (3-6 bullet points + Exam Point box)."
            "LONG" -> "Format as a Long 5/10 Mark Question with structured headings (Introduction, Principle, Parts, Working, Pros/Cons, Applications)."
            "DIFFERENCE" -> "Format as a Difference/Comparison Table with clear columns."
            "MACHINE" -> "Format as a Machine Details question (Name, Principle, Parts & Functions, Working, Specs, Exam Points)."
            "PRACTICAL" -> "Format as a Diploma Practical Experiment (Aim, Apparatus, Theory, Working, Calculation, Result, Viva Questions)."
            else -> "Determine the best student-friendly structure automatically."
        }

        val fullPrompt = "$formatInstruction\nLanguage instruction: $langInstruction\n\nStudent's Question:\n$prompt"

        val parts = mutableListOf<Part>()
        parts.add(Part(text = fullPrompt))

        if (imageBitmap != null) {
            parts.add(
                Part(
                    inlineData = InlineData(
                        mimeType = "image/jpeg",
                        data = imageBitmap.toBase64Jpeg()
                    )
                )
            )
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = parts)),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.3f)
        )

        try {
            val response = GeminiApiClient.service.generateContent(apiKey, request)
            val textResult = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            textResult ?: getOfflineFallbackAnswer(prompt, questionTypeFormat)
        } catch (e: Exception) {
            e.printStackTrace()
            getOfflineFallbackAnswer(prompt, questionTypeFormat)
        }
    }

    private fun getOfflineFallbackAnswer(prompt: String, format: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("kirschner") || lower.contains("beater") -> """
                Kirschner Beater (कीर्श्नर बीटर):
                
                Kirschner Beater Blow Room का एक बहुत ही महत्वपूर्ण Opening & Cleaning element है। इसे 3-Striker Pin Beater भी कहा जाता है।

                1. Definition:
                Kirschner Beater एक प्रकार का pinned beater होता है जिसमें 3 wooden lag bars पर sharp steel pins लगे होते हैं।

                2. Working Principle:
                यह impact plus combing action पर काम करता है। जब tufts इसके सम्पर्क में आते हैं, तो pins रुई के गुच्छों (tufts) में घुसकर उन्हें बहुत सफाई से खोलते (combing & opening action) हैं।

                3. Main Functions:
                - Cotton tufts को अधिक fine opening देना।
                - Light impurities और dust को remove करना।
                - Scutcher / Lap forming unit से ठीक पहले lap uniformity बनाना।

                4. Advantages:
                - Fibre damage कम होता है।
                - High opening efficiency प्रदान करता है।

                Exam Point:
                Kirschner Beater = 3 Lag Pinned Beater → Fine Opening + Dust Removal
            """.trimIndent()

            lower.contains("feed roller") || lower.contains("pedal") || lower.contains("difference") -> """
                Difference between Feed Roller and Pedal Roller (Blow Room):

                | Basis | Feed Roller | Pedal Roller |
                |---|---|---|
                | Construction | बेलनाकार (Cylindrical) ठोस रोलर | Pedal levers के साथ कार्य करने वाला सह-रोलर |
                | Function | रुई की शीट को बीटर तक uniform feed प्रदान करना | Variation के आधार पर nip control एवं thickness regulate करना |
                | Grip | Rigid Grip प्रदान करता है | Flexible Spring-loaded Nip Grip प्रदान करता है |
                | Regulation | इसमें automatic auto-leveller speed adjustment की आवश्यकता होती है | Pedal lever system स्वतः thickness control में मदद करता है |

                Exam Point:
                Feed Roller = Uniform Feeding | Pedal Roller = Thickness Control & Nip Grip
            """.trimIndent()

            lower.contains("blow room") -> """
                Blow Room (ब्लो रूम):

                1. Definition:
                Spinning mill का वह पहला डिपार्टमेंट जहाँ bales से प्राप्त कच्ची रुई (raw cotton) को खोला (opened), साफ (cleaned) और मिलाया (blended) जाता है।

                2. Main Objectives / Work of Blow Room:
                - Opening: Compressed bales से रुई के बड़े गुच्छों को छोटे tufts में तोड़ना।
                - Cleaning: Dirt, leaf, seed coat fragments और trash को अलग करना।
                - Blending/Mixing: विभिन्न bales की रुई को एकसमान गुण देने के लिए मिलाना।
                - Lap / Chute Formation: Carding machine के लिए lap या chute feed तैयार करना।

                3. Important Machines in Blow Room:
                - Bale Opener / Unifloc
                - Axi-Flow / Step Cleaner
                - Heavy Material Separator
                - Multi-Mixer
                - Kirschner Beater / Fine Cleaner
                - Scutcher / Chute Feed System

                Exam Point:
                Blow Room = Opening + Cleaning + Blending + Lap/Chute Formation
            """.trimIndent()

            lower.contains("rapier") || lower.contains("loom") -> """
                Rapier Loom (रेपियर लूम - Shuttleless Loom):

                1. Definition:
                Rapier Loom एक आधुनिक Shuttleless Loom है जिसमें weft yarn (बाना) को इंसर्ट करने के लिए flexible या rigid Rapier Tape / Sword का उपयोग किया जाता है।

                2. Principle:
                यह Mechanical Transfer Principle पर कार्य करता है। Single Rapier या Double Rapier (Giver & Taker) का उपयोग होता है।

                3. Working:
                - Giver Rapier बाने के धागे को ले जाकर shedding के मध्य तक पहुँचता है।
                - Centre में Taker Rapier धागे को पकड़ लेता है और दूसरी तरफ खींच लेता है।

                4. Speed & Production:
                - Speed: 500 - 800 picks per minute (ppm).
                - High versatility: सूती, ऊनी, रेशमी एवं मल्टीकलर फैब्रिक हेतु उपयुक्त।

                Exam Point:
                Rapier Loom = Mechanical Tape Insertion (Giver + Taker Rapier) → Versatile Weaving
            """.trimIndent()

            else -> """
                Textile Technology Diploma Study Guide:

                प्रश्न: "$prompt"

                उत्तर:
                Textile Technology Diploma सिलेबस के अनुसार इस विषय का मुख्य उद्देश्य रेशे (Fibre) से धागा (Yarn) और धागे से कपडा (Fabric) बनाने की प्रक्रियाओं एवं मशीनों को समझना है।

                मुख्य बिंदु (Key Highlights):
                1. Principle & Working: मशीन का मुख्य सिद्धान्त एवं उसका कार्यशील प्रवाह।
                2. Parameters: Machine speed, Production capacity, Quality testing standards (Uster %).
                3. Key Specifications: Diploma Exams हेतु उपयुक्त मशीन विवरण एवं पार्ट्स के नाम।

                Exam Point:
                परीक्षा में हमेशा उत्तर को Headings, Clear Points एवं Diagrams के साथ लिखें।
            """.trimIndent()
        }
    }
}
