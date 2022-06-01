package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PagedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;

import java.io.IOException;
import java.util.List;

public interface NewsService {

    /**
     *  Creates a new News Entry.
     *
     * @param news to create
     * @return created news entry
     */
    News createNews(NewsDto news) throws IOException;

    /**
     *  Gets all the available news entries.
     *
     *  @return all news entries
     */
    PagedNewsDto getAll(int page, int size);

    /**
     * Gets all the unread news entries for a user.
     *
     * @param mail the mail of the user for which we fetch the unread mails
     *
     * @return all unread news entries
     */
    PagedNewsDto getUnread(String mail, int page, int size);

    /**
     * Gets a news entry by id.
     *
     * @param id of the related news element
     * @param mail mail of the user which fetches the mail
     * @return corresponding news element
     */
    NewsDto getById(Long id, String mail);
}
