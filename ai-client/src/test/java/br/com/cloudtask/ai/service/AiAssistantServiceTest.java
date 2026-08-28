package br.com.cloudtask.ai.service;

import br.com.cloudtask.ai.config.CloudTaskAiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiAssistantServiceTest {

    private ChatClient.Builder chatClientBuilder;
    private ToolCallbackProvider mcpTools;
    private AiAssistantService service;

    @BeforeEach
    void setUp() {
        chatClientBuilder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        mcpTools = mock(ToolCallbackProvider.class);

        when(chatClientBuilder.defaultSystem(anyString())).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);

        service = new AiAssistantService(
                chatClientBuilder,
                mcpTools,
                new CloudTaskAiProperties("America/Sao_Paulo")
        );
    }

    @Test
    void excludesDeleteToolWithoutExplicitConfirmation() {
        when(mcpTools.getToolCallbacks()).thenReturn(new ToolCallback[]{
                tool("list_tasks"),
                tool("create_task"),
                tool("delete_task")
        });

        ToolCallbackProvider selected = service.selectTools("Exclua a tarefa 10");

        assertThat(toolNames(selected))
                .containsExactly("list_tasks", "create_task");
    }

    @Test
    void keepsDeleteToolWhenConfirmationTokenIsPresent() {
        ToolCallbackProvider selected = service.selectTools(
                "CONFIRMAR_EXCLUSAO: exclua a tarefa 10"
        );

        assertThat(selected).isSameAs(mcpTools);
        assertThat(service.hasDeletionConfirmation("confirmar_exclusao: tarefa 10")).isTrue();
    }

    private ToolCallback tool(String name) {
        ToolCallback callback = mock(ToolCallback.class);
        ToolDefinition definition = mock(ToolDefinition.class);
        when(callback.getToolDefinition()).thenReturn(definition);
        when(definition.name()).thenReturn(name);
        return callback;
    }

    private String[] toolNames(ToolCallbackProvider provider) {
        return Arrays.stream(provider.getToolCallbacks())
                .map(tool -> tool.getToolDefinition().name())
                .toArray(String[]::new);
    }
}
