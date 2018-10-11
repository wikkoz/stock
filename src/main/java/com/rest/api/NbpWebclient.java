package com.rest.api;

import com.domain.model.Rate;
import com.rest.mapper.NbpRatesMapper;
import com.rest.model.NbpDateRates;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class NbpWebclient {
    private static final int MAX_PERIOD = 93;

    public Flux<Rate> getRates(List<LocalDate> dates) {
        return splitBy3Months(dates).parallelStream()
                .map(period -> Pair.of(period.get(0), period.get(period.size()-1)))
                .map(pair -> restToApi(pair.getLeft(), pair.getRight()))
                .reduce(Flux.empty(), Flux::concat);
    }

    private List<List<LocalDate>> splitBy3Months(List<LocalDate> dates) {
        return IntStream.range(0, (dates.size() + MAX_PERIOD - 1) / MAX_PERIOD)
                .mapToObj(i -> dates.subList(i * MAX_PERIOD, Math.min(MAX_PERIOD * (i + 1), dates.size())))
                .collect(Collectors.toList());
    }

    private Flux<Rate> restToApi(LocalDate startDate, LocalDate endDate) {
        WebClient webClient = WebClient.create("http://api.nbp.pl/api/exchangerates/tables/");
        return webClient.get().uri("A/" +
                startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "/" +
                endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "/")
                .retrieve()
                .bodyToFlux(NbpDateRates.class)
                .flatMap(nbpDateRate -> Flux.fromIterable(NbpRatesMapper.map(nbpDateRate)))
                .onErrorResume(WebClientResponseException.class, a -> Flux.empty());
    }
}
