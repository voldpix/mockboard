package dev.mockboard.web.api;

import dev.mockboard.service.MockExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

//@RestController
@RequestMapping("/m/{apiKey}")
@RequiredArgsConstructor
public class MockExecutionController {

    private final MockExecutionService mockExecutionService;

//    @RequestMapping(value = "/**", method = {
//            RequestMethod.GET,
//            RequestMethod.POST,
//            RequestMethod.PUT,
//            RequestMethod.DELETE,
//            RequestMethod.OPTIONS,
//            RequestMethod.PATCH,})
//    public ResponseEntity<String> executeMock(@PathVariable String apiKey, HttpServletRequest request) {
//        var mockPath = RequestUtils.extractMockPath(request, apiKey);
//        var method = request.getMethod();
//
//        // todo: when empty send empty response, not exception
//        var mockRule = mockExecutionService.findMatchingRule(apiKey, mockPath, method)
//                .orElseThrow(() -> new NotFoundException(
//                        "No mock found for " + method + " " + mockPath
//                ));
//
//        var headers = new HttpHeaders();
//        if (mockRule.getHeaders() != null && !mockRule.getHeaders().isEmpty()) {
//            mockRule.getHeaders().forEach(headers::add);
//        }
//
//        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//        return ResponseEntity
//                .status(mockRule.getStatusCode())
//                .headers(headers)
//                .body(mockRule.getBody());
//    }
}
