package com.domain.services

import com.domain.model.Rate
import com.rest.api.NbpWebclient
import spock.lang.Specification

import java.time.LocalDate

class WebRatesRepositoryTest extends Specification {
    NbpWebclient nbpWebclient = Mock(NbpWebclient)
    WebRatesRepository webRatesRepository = new WebRatesRepository(nbpWebclient)

    def "should get one period when there is no saved data"() {
        given:
        LocalDate startDate = LocalDate.of(2018, 12, 20)
        LocalDate endDate = LocalDate.of(2018, 12, 30)

        when:
        List<List<LocalDate>> periods = webRatesRepository.splitDatesInFullPeriods(startDate, endDate, [])

        then:
        periods.size() == 1;
    }

    def "should get two periods when there is one day in saved data"() {
        given:
        LocalDate startDate = LocalDate.of(2018, 12, 20)
        LocalDate endDate = LocalDate.of(2018, 12, 30)
        List<LocalDate> savedDates = [LocalDate.of(2018, 12, 24)]

        when:
        List<List<LocalDate>> periods = webRatesRepository.splitDatesInFullPeriods(startDate, endDate, savedDates)

        then:
        periods.size() == 2;
        periods[0].size() == 4
        periods[1].size() == 6
    }
}
