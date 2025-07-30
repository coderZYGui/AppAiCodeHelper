package com.sunny.appaicodehelper.ai;

import com.sunny.appaicodehelper.guardrail.SafeInputGuardrail;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;

import java.util.List;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description:
 */


//@AiService
@InputGuardrails(SafeInputGuardrail.class)
public interface AiCoderHelperService {


    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);


    // 结构化输出(json格式)

    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String userMessage);

    Result<String> chatWithRag(String s);

    record Report(String name, List<String>suggestionList){

    }
}
