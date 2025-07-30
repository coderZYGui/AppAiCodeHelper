package com.sunny.appaicodehelper.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;

import java.util.Set;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description:
 */
public class SafeInputGuardrail implements InputGuardrail {


    private static final Set<String> sensitiveWords = Set.of("kill", "evil");


    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        // 获取用户输入并转换为小写,确保大小写不敏感
        String inputText = userMessage.singleText().toLowerCase();
        String[] words = inputText.split("\\W+");
        for (String word : words) {
            if (sensitiveWords.contains(word)) {
                return fatal("输入包含敏感词: " + word);
            }
        }
        return success();
    }
}
