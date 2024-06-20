package com.example.demoDMS1.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListPaginatedObjectsResponse {
    private List<String> objectKeys;
    List<String> contentType;
    List<Map<String,String>> metadata;
    String continuationToken;
    List<String> lastModifiedTime;
    List<Long> fileSizes;
}
