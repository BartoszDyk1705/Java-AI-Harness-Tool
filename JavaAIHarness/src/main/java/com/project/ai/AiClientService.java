package com.project.ai;

public class AiClientService {
    private String lastSummary="";
    private boolean useCloudFallback = false;
    // PART TO REPLACE WITH ACTUAL LLM CALL
    public String sendPromptWithTools(String prompt) throws Exception{
        // LLM thinking simulation
        Thread.sleep(2000);

        if(prompt.equalsIgnoreCase("error")){
            throw new RuntimeException("Simulated connection error to http://127.0.0.1:1234/v1");
        }

        String mode = useCloudFallback ? "NVIDIA NIM Cloud" : "Local LM Studio (Saluki 27B)";
        this.lastSummary = "Summary generated via [" + mode + "] for prompt: \"" +prompt + "\"";
        return this.lastSummary;
    }

    public void switchEndPoint(boolean useNimCloud){
        this.useCloudFallback = useNimCloud;
    }

    public String getLastSummary(){
        return lastSummary;
    }
}