package com.magicgate.link.domain.advisor;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * @Author yangyangsheep
 * @Description 示例增强处理
 * @CreateTime 2025/7/1 21:14
 */
public class SafeGuardAdvisor implements CallAdvisor, StreamAdvisor {
    /**
     * Return the name of the advisor.
     *
     * @return the advisor name.
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     *
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * @param chatClientRequest
     * @param streamAdvisorChain
     *
     * @return
     */
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        return !CollectionUtils.isEmpty(this.sensitiveWords) && this.sensitiveWords.stream()
                .anyMatch((w) -> chatClientRequest.prompt()
                        .getContents()
                        .contains(w)) ? Flux.just(this.createFailureResponse(chatClientRequest)) : streamAdvisorChain.nextStream(chatClientRequest);
    }

    private static final String DEFAULTFAILURERESPONSE = "I'm unable to respond to that due to sensitive content. Could we rephrase or discuss something else?";
    private static final int DEFAULTORDER = 0;
    private final String failureResponse;
    private final List<String> sensitiveWords;
    private final int order;

    public SafeGuardAdvisor(List<String> sensitiveWords) {
        this(sensitiveWords, "I'm unable to respond to that due to sensitive content. Could we rephrase or discuss something else?", 0);
    }

    public SafeGuardAdvisor(List<String> sensitiveWords, String failureResponse, int order) {
        Assert.notNull(sensitiveWords, "Sensitive words must not be null!");
        Assert.notNull(failureResponse, "Failure response must not be null!");
        this.sensitiveWords = sensitiveWords;
        this.failureResponse = failureResponse;
        this.order = order;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        return !CollectionUtils.isEmpty(this.sensitiveWords) && this.sensitiveWords.stream()
                .anyMatch((w) -> chatClientRequest.prompt()
                        .getContents()
                        .contains(w)) ? this.createFailureResponse(chatClientRequest) : callAdvisorChain.nextCall(chatClientRequest);
    }


    private ChatClientResponse createFailureResponse(ChatClientRequest chatClientRequest) {
        return ChatClientResponse.builder()
                .chatResponse(ChatResponse.builder().generations(List.of(new Generation(new AssistantMessage(this.failureResponse)))).build())
                .context(Map.copyOf(chatClientRequest.context()))
                .build();
    }


    public static final class Builder {
        private List<String> sensitiveWords;
        private String failureResponse = "I'm unable to respond to that due to sensitive content. Could we rephrase or discuss something else?（我不能够回答这个问题，因为其中包含非法内容）";
        private int order = 0;

        private Builder() {
        }

        public Builder sensitiveWords(List<String> sensitiveWords) {
            this.sensitiveWords = sensitiveWords;
            return this;
        }

        public Builder failureResponse(String failureResponse) {
            this.failureResponse = failureResponse;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public SafeGuardAdvisor build() {
            return new SafeGuardAdvisor(this.sensitiveWords, this.failureResponse, this.order);
        }
    }
}
