package com.sunny.appaicodehelper.ai;

import dev.langchain4j.service.SystemMessage;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description:
 */


//@AiService
public interface AiCoderHelperService {


    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);
}
