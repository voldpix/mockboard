package dev.mockboard.web.api;

import dev.mockboard.service.PreBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pre")
@RequiredArgsConstructor
public class PreBoardController {

    private final PreBoardService preBoardService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPreBoards() {
        return new ResponseEntity<>(preBoardService.getAppConfigs(), HttpStatus.OK);
    }
}
