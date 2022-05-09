package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;

import java.util.List;

public interface ArtistService {

    /** Returns a list of artists with matching criteria or all artists if every attribute is null
     * @param artistSearchDto Dto that contains search parameters
     * @return a list of artists with matching criteria or all artists
     */
    List<Artist> find(ArtistSearchDto artistSearchDto);
}
