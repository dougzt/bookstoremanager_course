package com.rodrigopeleias.bookstoremanager.publishers.service;

import com.rodrigopeleias.bookstoremanager.publishers.builder.PublisherDTOBuilder;
import com.rodrigopeleias.bookstoremanager.publishers.dto.PublisherDTO;
import com.rodrigopeleias.bookstoremanager.publishers.entity.Publisher;
import com.rodrigopeleias.bookstoremanager.publishers.exception.PublisherAlreadyExistsException;
import com.rodrigopeleias.bookstoremanager.publishers.exception.PublisherNotFoundException;
import com.rodrigopeleias.bookstoremanager.publishers.mapper.PublisherMapper;
import com.rodrigopeleias.bookstoremanager.publishers.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PublisherServiceTest {

    private final static PublisherMapper publisherMapper = PublisherMapper.INSTANCE;

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherService publisherService;

    private PublisherDTOBuilder publisherDTOBuilder;

    @BeforeEach
    void setUp() {
        publisherDTOBuilder = PublisherDTOBuilder.builder().build();
    }

    @Test
    void whenNewPublisherIsInformedThenItShouldBeCreated() {
        PublisherDTO expectedPublisherToCreateDTO = publisherDTOBuilder.buildPublisherDTO();
        Publisher expectedPublisherCreated = publisherMapper.toModel(expectedPublisherToCreateDTO);

        when(publisherRepository.findByNameOrCode(expectedPublisherToCreateDTO.getName(), expectedPublisherToCreateDTO.getCode()))
                .thenReturn(Optional.empty());
        when(publisherRepository.save(expectedPublisherCreated)).thenReturn(expectedPublisherCreated);

        PublisherDTO createdPublisherDTO = publisherService.create(expectedPublisherToCreateDTO);

        assertThat(createdPublisherDTO, is(equalTo(expectedPublisherToCreateDTO)));
    }

    @Test
    void whenExistingPublisherIsInformedThenAnExceptionShouldBeThrown() {
        PublisherDTO expectedPublisherToCreateDTO = publisherDTOBuilder.buildPublisherDTO();
        Publisher expectedPublisherDuplicated = publisherMapper.toModel(expectedPublisherToCreateDTO);

        when(publisherRepository.findByNameOrCode(expectedPublisherToCreateDTO.getName(), expectedPublisherToCreateDTO.getCode()))
                .thenReturn(Optional.of(expectedPublisherDuplicated));


        assertThrows(PublisherAlreadyExistsException.class, () -> publisherService.create(expectedPublisherToCreateDTO));
    }

    @Test
    void whenValidIdIsGivenThenAPublisherShouldBeReturned() {
        PublisherDTO expectedPublisherFoundDTO = publisherDTOBuilder.buildPublisherDTO();
        Publisher expectedPublisherFound = publisherMapper.toModel(expectedPublisherFoundDTO);

        when(publisherRepository.findById(expectedPublisherFoundDTO.getId())).thenReturn(Optional.of(expectedPublisherFound));

        PublisherDTO foundPublisherDTO = publisherService.findById(expectedPublisherFoundDTO.getId());

        assertThat(foundPublisherDTO, is(equalTo(expectedPublisherFoundDTO)));
    }

    @Test
    void whenInvalidIdIsGivenThenAnExceptionShouldBrThrown() {
        PublisherDTO expectedPublisherFoundDTO = publisherDTOBuilder.buildPublisherDTO();

        when(publisherRepository.findById(expectedPublisherFoundDTO.getId())).thenReturn(Optional.empty());

        assertThrows(PublisherNotFoundException.class, () -> publisherService.findById(expectedPublisherFoundDTO.getId()));
    }

    @Test
    void whenListPublishersIsCalledThenItShouldBeReturned() {
        PublisherDTO expectedPublisherFoundDTO = publisherDTOBuilder.buildPublisherDTO();
        Publisher expectedPublisherFound = publisherMapper.toModel(expectedPublisherFoundDTO);

        when(publisherRepository.findAll()).thenReturn(Collections.singletonList(expectedPublisherFound));

        List<PublisherDTO> foundPublishersDTO = publisherService.findAll();

        assertThat(foundPublishersDTO.size(), is(1));
        assertThat(foundPublishersDTO.get(0), is(equalTo(expectedPublisherFoundDTO)));
    }

    @Test
    void whenListPublishersIsCalledThenAnEmptyListShouldBeReturned() {
        when(publisherRepository.findAll()).thenReturn(Collections.emptyList());

        List<PublisherDTO> foundPublishersDTO = publisherService.findAll();

        assertThat(foundPublishersDTO.size(), is(0));
    }
}
