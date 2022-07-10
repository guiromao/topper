package co.topper.domain.controller;

import co.topper.domain.data.dto.SearchDto;
import co.topper.domain.data.dto.TrackDto;
import co.topper.domain.service.MusicSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static co.topper.configuration.constants.ControllerConstants.*;

@RestController
@RequestMapping(APP + VERSION + "/search")
public class SearchController {

    private final MusicSearchService searchService;

    public SearchController(MusicSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public Set<TrackDto> search(@RequestBody SearchDto searchDto) {
        return searchService.searchTracks(searchDto.getTrackName());
    }

}
