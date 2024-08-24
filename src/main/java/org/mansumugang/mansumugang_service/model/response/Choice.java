package org.mansumugang.mansumugang_service.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.mansumugang.mansumugang_service.model.request.Message;

import java.io.Serializable;

@Data
public class Choice implements Serializable {
    private Integer index;
    private Message message;
    @JsonProperty("finish_reason")
    private String finishReason;
}
