classDiagram
direction TB

    class Main {
        +main(String[] args)$ void
    }

    class Config {
        -String localBaseUrl
        -String nimBaseUrl
        -String apiKey
        -String modelName
        -boolean useCloudFallback
        +loadFromEnv()$ Config
        +getActiveBaseUrl() String
        +getModelName() String
    }

    class MainWindow {
        -JTextArea chatArea
        -JTextField inputField
        -JButton sendButton
        -JButton saveReportButton
        -JProgressBar progressBar
        -AiClientService aiService
        -SessionSerializer sessionSerializer
        +MainWindow(AiClientService aiService)
        -initComponents() void
        -bindListeners() void
        +appendMessage(String role, String text) void
        +showErrorDialog(Exception ex) void
    }

    class AiTaskWorker {
        -String userPrompt
        -AiClientService aiService
        -MainWindow window
        #doInBackground() String
        #done() void
    }

    class AiClientService {
        -OpenAIClient client
        -Config config
        -ConversationSession session
        -Map~String, Tool~ toolRegistry
        +AiClientService(Config config)
        +registerTool(Tool tool) void
        +sendPromptWithTools(String prompt) String
        -handleToolCalls(List~ToolCall~ calls) String
        +switchEndpoint(boolean useNimCloud) void
        +getSession() ConversationSession
    }

    class ConversationSession {
        <<Serializable>>
        -long serialVersionUID$
        -String sessionId
        -LocalDateTime createdAt
        -List~ChatMessage~ messages
        -String lastSummary
        +addMessage(String role, String content) void
        +getMessages() List~ChatMessage~
        +clearHistory() void
        +getLastSummary() String
    }

    class Tool {
        <<interface>>
        +getName() String
        +getDescription() String
        +getParameterSchema() Map~String, String~
        +execute(Map~String, Object~ args) String
    }

    class AbstractTool {
        <<abstract>>
        #String name
        #String description
        #AbstractTool(String name, String desc)
        +getName() String
        +getDescription() String
        #requireStringArg(Map args, String key) String
        #logExecution(String status) void
    }

    class ReadFileTool {
        -FileAccessManager fileManager
        +ReadFileTool(FileAccessManager fm)
        +getParameterSchema() Map~String, String~
        +execute(Map~String, Object~ args) String
    }

    class SaveReportTool {
        -Path reportsDir
        +SaveReportTool(Path reportsDir)
        +getParameterSchema() Map~String, String~
        +execute(Map~String, Object~ args) String
    }

    class ListFilesTool {
        -FileAccessManager fileManager
        +ListFilesTool(FileAccessManager fm)
        +getParameterSchema() Map~String, String~
        +execute(Map~String, Object~ args) String
    }

    class FileAccessManager {
        -long MAX_FILE_BYTES$
        -Path workspaceRoot
        -Set~String~ allowedExtensions
        +readFileSafely(String relPath) String
        +listWorkspaceFiles() List~String~
        -validatePath(String relPath) Path
    }

    class SessionSerializer {
        +saveSession(ConversationSession session, Path file) void
        +loadSession(Path file) ConversationSession
    }

    class ToolExecutionException {
        <<Exception>>
        -String toolName
        +ToolExecutionException(String tool, String msg)
        +getToolName() String
    }

    class ModelUnavailableException {
        <<RuntimeException>>
        -String endpointUrl
        +ModelUnavailableException(String url, String msg)
        +getEndpointUrl() String
    }

    Main ..> MainWindow : initializes
    Main ..> Config : loads
    MainWindow ..> AiTaskWorker : spawns
    MainWindow --> AiClientService : uses
    AiTaskWorker --> AiClientService : invokes in background
    AiClientService *-- ConversationSession : manages
    AiClientService o-- Tool : registers 0..*
    AbstractTool ..|> Tool : implements
    ReadFileTool --|> AbstractTool : extends
    SaveReportTool --|> AbstractTool : extends
    ListFilesTool --|> AbstractTool : extends
    ReadFileTool --> FileAccessManager : delegates I/O
    ListFilesTool --> FileAccessManager : delegates I/O
    SessionSerializer ..> ConversationSession : serializes (.ser)
    AbstractTool ..> ToolExecutionException : throws
    AiClientService ..> ModelUnavailableException : throws