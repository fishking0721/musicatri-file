package org.fishking0721.oss.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "netease-api", url = "http://localhost:3000")
public interface NeteaseApi {

    // 搜索歌曲（获取歌曲 ID）
    @GetMapping("/search")
    String search(@RequestParam("keywords") String keywords);

    // 获取歌曲 URL
    @GetMapping("/song/url/v1")
    JsonNode getSongUrl(@RequestParam("id") String songId, @RequestParam("level") String level);

    // 获取歌曲详情
    @GetMapping("/song/detail")
    JsonNode getSongDetail(@RequestParam("ids") String songIds);

//    @GetMapping("/song/dynamic/cover")
//    JsonNode getSongDynamicCover(@RequestParam("id") String songId);
}
