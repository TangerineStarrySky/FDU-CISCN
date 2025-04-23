//package com.example.smsdetection.utils;
//
//import ai.mlc.mlcllm.MLCChat;
//import java.util.Arrays;
//
//public class ChatClientLocal {
//    public static final int QWEN0_5b = 0;
//    public static final int QWEN1_5b = 1;
//    public static final int QWEN3b = 2;
//    public static final String[] MODELS = {"qwen2.5-0.5b-instruct", "qwen2.5-1.5b-instruct", "qwen2.5-3b-instruct"};
//
//    private MLCChat mlcChat;
//
//    /**
//     * 初始化本地模型。
//     *
//     * @param modelPath 模型路径（相对于 assets 目录）
//     */
//    public ChatClientLocal(String modelPath) {
//        // 初始化 MLC-LLM 运行时
//        mlcChat = new MLCChat(null); // 参数为 Context，本地测试可传 null
//        mlcChat.loadModel("file:///android_asset/" + modelPath); // 加载本地模型
//    }
//
//    /**
//     * 调用本地模型生成回复。
//     *
//     * @param input 用户输入
//     * @return 模型生成的回复
//     */
//    public String callWithMessage(String input) {
//        // 构造系统提示和用户输入
//        String systemPrompt = "你是一个反诈领域的专家，你会接收到一段短信息，请你判断该信息是否为诈骗短信，返回\"是\"或者\"否\"。";
//        String fullInput = systemPrompt + "\n\n用户输入: " + input;
//
//        // 调用模型生成回复
//        return mlcChat.generate(fullInput);
//    }
//
//    /**
//     * 调用本地模型生成详细分析。
//     *
//     * @param input 用户输入
//     * @return 模型生成的详细分析
//     */
//    public String callForDetail(String input) {
//        // 构造系统提示和用户输入
//        String systemPrompt = "你是一个反诈领域的专家，你会收到一条诈骗短信，请你描述该信息可能属于的诈骗类别并阐述其诈骗套路。\n" +
//                "(诈骗类别主要有：刷单返利类诈骗，虚假网络投资理财类诈骗，虚假网络贷款类诈骗，冒充电商物流客服类诈骗，冒充公检法类诈骗，" +
//                "虚假征信类诈骗，虚假购物、服务类诈骗，冒充领导、熟人类诈骗，网络游戏产品虚假交易类诈骗，婚恋、交友类诈骗)";
//        String fullInput = systemPrompt + "\n\n用户输入: " + input;
//
//        // 调用模型生成回复
//        return mlcChat.generate(fullInput);
//    }
//
//    public static void main(String[] args) {
//        try {
//            // 初始化本地模型
//            ChatClientLocal client = new ChatClientLocal("Qwen2.5-0.5B-Instruct-q4f16_1-MLC");
//
//            // 测试输入
//            String neg_text = "新台新运,牛年牛气,100送288直达,500送688直达,一水无限制,真诚相邀,机会不容错过,加Q2747491745";
//            String pos_text = "我为什么觉得花千骨这部片是一场巨大的审美钓鱼呢…一点格调都没有";
//
//            // 调用本地模型
//            String result = client.callWithMessage(neg_text);
//            System.out.println("是否为诈骗短信: " + result);
//
//            String detail = client.callForDetail(neg_text);
//            System.out.println("详细分析: " + detail);
//
//            String result2 = client.callWithMessage(pos_text);
//            System.out.println("是否为诈骗短信: " + result2);
//        } catch (Exception e) {
//            // 使用日志框架记录异常信息
//            System.err.println("An error occurred while calling the local model: " + e.getMessage());
//        }
//        System.exit(0);
//    }
//}