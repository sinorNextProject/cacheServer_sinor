package com.sinor.cache.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainCacheRequest {

    private Map<String, String> requestBody;
}
