package network.configuration;

import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.api.TarantoolClientConfig;
import io.tarantool.driver.api.TarantoolClusterAddressProvider;
import io.tarantool.driver.api.TarantoolServerAddress;
import io.tarantool.driver.api.retry.TarantoolRequestRetryPolicies;
import io.tarantool.driver.auth.SimpleTarantoolCredentials;
import io.tarantool.driver.core.ClusterTarantoolTupleClient;
import io.tarantool.driver.core.ProxyTarantoolTupleClient;
import io.tarantool.driver.core.RetryingTarantoolTupleClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class TarantoolConfig {

    @Bean
    public TarantoolClient tarantoolClient(
            @Value("${tarantool.nodes}") String nodes,
            @Value("${tarantool.username}") String username,
            @Value("${tarantool.password}") String password) {

        SimpleTarantoolCredentials credentials = new SimpleTarantoolCredentials(username, password);

        TarantoolClientConfig config = new TarantoolClientConfig.Builder()
                .withCredentials(credentials)
                .withRequestTimeout(1000*60)
                .build();

        TarantoolClusterAddressProvider provider = () -> {
            ArrayList<TarantoolServerAddress> addresses = new ArrayList<>();

            for (String node: nodes.split(",")) {
                String[] address = node.split(":");
                addresses.add(new TarantoolServerAddress(address[0], Integer.parseInt(address[1])));
            }

            return addresses;
        };

        ClusterTarantoolTupleClient clusterClient = new ClusterTarantoolTupleClient(config, provider);
        ProxyTarantoolTupleClient proxyClient = new ProxyTarantoolTupleClient(clusterClient);

        return new RetryingTarantoolTupleClient(
                proxyClient,
                TarantoolRequestRetryPolicies.byNumberOfAttempts(
                        10, e -> e.getMessage().contains("Unsuccessful attempt")
                ).build());
    }
}
