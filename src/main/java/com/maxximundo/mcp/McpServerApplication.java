package com.maxximundo.mcp;

import com.maxximundo.mcp.tools.service.MaxximundoTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpServerApplication.class, args);
	}
	
    
    @Bean
    public ToolCallbackProvider toolService(MaxximundoTool maxximundoTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(maxximundoTool)
                .build();
    }


}
