package br.com.cloudtask.ai.service;

import br.com.cloudtask.ai.config.CloudTaskAiProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Locale;

@Service
public class AiAssistantService {

    public static final String DELETE_CONFIRMATION_TOKEN = "CONFIRMAR_EXCLUSAO";

    private static final String SYSTEM_PROMPT = """
            Você é o assistente de tarefas do CloudTask Enterprise.

            Regras obrigatórias:
            - Use as ferramentas MCP disponíveis para consultar ou alterar tarefas. Não invente resultados de operações.
            - Quando criar tarefas, preserve exatamente a intenção do usuário e use apenas status e prioridades aceitos pelas tools.
            - Se faltar informação opcional, prefira os defaults definidos pela tool em vez de inventar dados.
            - Para excluir uma tarefa, só execute a operação se a mensagem do usuário contiver explicitamente o token CONFIRMAR_EXCLUSAO.
            - Se o usuário pedir exclusão sem esse token, explique que é necessária confirmação explícita e não diga que a tarefa foi removida.
            - Responda em português do Brasil, de forma curta e objetiva, informando o resultado real da operação.
            """;

    private final ChatClient chatClient;
    private final ToolCallbackProvider mcpTools;
    private final ZoneId timeZone;

    public AiAssistantService(ChatClient.Builder chatClientBuilder,
                              ToolCallbackProvider mcpTools,
                              CloudTaskAiProperties properties) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
        this.mcpTools = mcpTools;
        this.timeZone = ZoneId.of(properties.timeZone());
    }

    public String ask(String message) {
        ToolCallbackProvider allowedTools = selectTools(message);
        String contextualizedMessage = "Data atual: %s.%nSolicitação: %s"
                .formatted(LocalDate.now(timeZone), message);

        return chatClient
                .prompt()
                .user(contextualizedMessage)
                .tools(allowedTools)
                .call()
                .content();
    }

    ToolCallbackProvider selectTools(String message) {
        if (hasDeletionConfirmation(message)) {
            return mcpTools;
        }

        ToolCallback[] safeTools = Arrays.stream(mcpTools.getToolCallbacks())
                .filter(tool -> !"delete_task".equals(tool.getToolDefinition().name()))
                .toArray(ToolCallback[]::new);

        return ToolCallbackProvider.from(safeTools);
    }

    boolean hasDeletionConfirmation(String message) {
        return message != null
                && message.toUpperCase(Locale.ROOT).contains(DELETE_CONFIRMATION_TOKEN);
    }
}
