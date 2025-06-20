package com.example.smsdetection.utils;

import ai.mlc.mlcllm.MLCEngine
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionMessage
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionRole
import ai.mlc.mlcllm.OpenAIProtocol.StreamOptions
import kotlinx.coroutines.runBlocking

object ModelInvoker {
    @JvmStatic
    fun chat1(input: String, engine: MLCEngine): String {
        val messages = listOf(
            ChatCompletionMessage(ChatCompletionRole.system, "你是一个反诈领域的专家，你会接收到一段短信息，请你判断该信息是否为诈骗短信，返回\"是\"或者\"否\"。", null, null, null),
            ChatCompletionMessage(ChatCompletionRole.user, input, null, null, null)
        )

        return runBlocking {
            val completions = engine.chat.completions
            val responseChannel = completions.create(
                messages = messages,
                model = "qwen3",
                stream = true,
                stream_options = StreamOptions(include_usage = true)
            )

            val fullResponse = StringBuilder()

            for (response in responseChannel) {
                val content = response.choices.firstOrNull()?.delta?.content
                if (content != null) {
                    fullResponse.append(content)
                }
            }

            fullResponse.toString()
        }
    }
    @JvmStatic
    fun chat2(input: String, engine: MLCEngine): String {
        val messages = listOf(
            ChatCompletionMessage(ChatCompletionRole.system,
                ("你是一个反诈领域的专家，你会收到一条诈骗短信，请你描述该信息可能属于的诈骗类别并阐述其诈骗套路。" +
                        "(诈骗类别主要有：刷单返利类诈骗，虚假网络投资理财类诈骗，虚假网络贷款类诈骗，冒充电商物流客服类诈骗，冒充公检法类诈骗，" +
                        "虚假征信类诈骗，虚假购物、服务类诈骗，冒充领导、熟人类诈骗，网络游戏产品虚假交易类诈骗，婚恋、交友类诈骗)"), null, null, null),
            ChatCompletionMessage(ChatCompletionRole.user, input, null, null, null)
        )

        return runBlocking {
            val completions = engine.chat.completions
            val responseChannel = completions.create(
                messages = messages,
                model = "qwen3",
                stream = true,
                stream_options = StreamOptions(include_usage = true)
            )

            val fullResponse = StringBuilder()

            for (response in responseChannel) {
                val content = response.choices.firstOrNull()?.delta?.content
                if (content != null) {
                    fullResponse.append(content)
                }
            }

            fullResponse.toString()
        }
    }
}