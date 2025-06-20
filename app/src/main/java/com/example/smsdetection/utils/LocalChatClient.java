package com.example.smsdetection.utils;

import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ai.mlc.mlcllm.Completions;
import ai.mlc.mlcllm.MLCEngine;
import ai.mlc.mlcllm.OpenAIProtocol;
import kotlinx.coroutines.GlobalScope;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlin.coroutines.EmptyCoroutineContext;


public class LocalChatClient {
    private static MLCEngine engine;

    static {
        // 假设你的模型 bundle 路径是固定的
        String modelPath = "asset:///models/converted_model_local";
        String libPath = "qwen3_q4f16_1_abc076dbb58ea1eebc5177c24b5b45d9";

        // 初始化引擎并加载模型库
        engine = new MLCEngine();
        engine.reload(modelPath, libPath);
    }

    public static String callWithMessage(String input) throws Exception {
        return ModelInvoker.chat1(input, engine);
    }

    public static String callForDetail(String input) throws Exception {
        return ModelInvoker.chat2(input, engine);
    }
}
