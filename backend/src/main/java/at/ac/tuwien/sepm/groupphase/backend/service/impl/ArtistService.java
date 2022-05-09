package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class ArtistService implements at.ac.tuwien.sepm.groupphase.backend.service.ArtistService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public List<Artist> find(ArtistSearchDto artistSearchDto) {
        LOGGER.debug("Find artists with {}", artistSearchDto);
        if (artistSearchDto.name() == null && artistSearchDto.maxRecords() == null) {
            return artistRepository.findAll();
        } else if (artistSearchDto.name() != null && artistSearchDto.maxRecords() == null) {
            return artistRepository.findByNameContaining(artistSearchDto.name());
        } else if (artistSearchDto.name() == null) {
            Pageable topResults = PageRequest.of(0, artistSearchDto.maxRecords());
            return artistRepository.findAll(topResults).stream().toList();
        } else {
            Pageable topResults = PageRequest.of(0, artistSearchDto.maxRecords());
            return artistRepository.findByNameContaining(artistSearchDto.name(), topResults);
        }

    }
}
