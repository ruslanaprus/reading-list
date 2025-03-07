package com.peach.ai.queue.messageDTO;


import com.peach.ai.books.ReadingListRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadingListMessage implements Serializable {
    private String requestId;
    private ReadingListRequest request;
}