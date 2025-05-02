package com.peach.ai.books.queue.messageDTO;


import com.peach.ai.books.model.ReadingListRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadingListMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requestId;
    private ReadingListRequest request;
}