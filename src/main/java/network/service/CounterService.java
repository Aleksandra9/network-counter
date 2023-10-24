package network.service;

import io.tarantool.driver.api.TarantoolClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CounterService {
    private final TarantoolClient tarantoolClient;

    public CounterService(TarantoolClient tarantoolClient) {
        this.tarantoolClient = tarantoolClient;
    }


    public Number getCount(String userId) {
        try {
            var obj = (List<Object>) tarantoolClient.call("get_data", userId).get().get(0);
            if (obj.size() > 0) {
                var arr = (List<Object>) obj.get(0);
                return (Number) arr.get(1);
            }
            return 0;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void addCount(String userId) {
        tarantoolClient.call("add_data", userId );
    }

    public void minusCount(String userId) {
        tarantoolClient.call("minus_data", userId);
    }

}
