package com.project.ui;

import com.project.ai.AiClientService;
import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;

public class AiTaskWorker extends SwingWorker<String, Void>{
    private final String userPrompt;
    private final AiClientService aiService;
    private final MainWindow window;

    public AiTaskWorker(String userPrompt, AiClientService aiService, MainWindow window){
        this.userPrompt = userPrompt;
        this.aiService = aiService;
        this.window = window;
    }

    @Override
    protected String doInBackground() throws Exception {
        return aiService.sendPromptWithTools(userPrompt);
    }

    @Override
    public void done(){
        window.setLoadingState(false);
        try {
            String response = get();
            window.appendMessage("AI Assistant", response);
            window.enableReportButton(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            window.showErrorDialog(e);
        } car
    }
}