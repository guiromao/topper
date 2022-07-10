package co.topper.domain.service;

import co.topper.domain.data.converter.TrackConverter;
import co.topper.domain.data.dto.TrackDto;
import co.topper.domain.data.entity.AlbumEntity;
import co.topper.domain.data.entity.ArtistEntity;
import co.topper.domain.data.entity.TrackEntity;
import co.topper.domain.data.repository.AlbumRepository;
import co.topper.domain.data.repository.ArtistRepository;
import co.topper.domain.data.repository.TrackRepository;
import co.topper.domain.exception.InvalidArgumentsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopServiceImpl implements TopService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final TrackConverter trackConverter;

    @Autowired
    public TopServiceImpl(TrackRepository trackRepository,
                          AlbumRepository albumRepository,
                          ArtistRepository artistRepository,
                          TrackConverter trackConverter) {
        this.trackRepository = trackRepository;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.trackConverter = trackConverter;
    }

    @Override
    public List<TrackDto> getTop(Integer limit, Integer offset) {
        if (limit < 1 || offset < 0) {
            throw new InvalidArgumentsException(limit, offset);
        }

        Pageable pageable = PageRequest.of(offset, limit);

        List<TrackEntity> tracks = trackRepository.getTop(pageable);
        Set<AlbumEntity> albums = fetchAlbums(extractAlbumIds(tracks));
        Set<ArtistEntity> artists = fetchArtists(extractArtistIds(tracks));

        return trackConverter.toDtoList(tracks, albums, artists);
    }

    private Set<AlbumEntity> fetchAlbums(Set<String> albumIds) {
        return (Set<AlbumEntity>) albumRepository.findAllById(albumIds);
    }

    private Set<ArtistEntity> fetchArtists(Set<String> artistIds) {
        return (Set<ArtistEntity>) artistRepository.findAllById(artistIds);
    }

    private Set<String> extractAlbumIds(List<TrackEntity> tracks) {
        return tracks.stream()
                .map(TrackEntity::getAlbumId)
                .collect(Collectors.toSet());
    }

    private Set<String> extractArtistIds(List<TrackEntity> tracks) {
        return tracks.stream()
                .map(TrackEntity::getArtistIds)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
