package org.ehcache.sample;

import static java.net.URI.create;
import static org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder.clusteredDedicated;
import static org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder.cluster;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.heap;
import static org.ehcache.config.units.MemoryUnit.MB;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.ehcache.CacheManager;
import org.ehcache.CachePersistenceException;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.units.MemoryUnit;
import org.slf4j.Logger;

public class ClusteredProgrammatic {
  private static final Logger LOGGER = getLogger(ClusteredProgrammatic.class);

  public static void main(String[] args) throws CachePersistenceException, InterruptedException {
    final List<String> argList = Arrays.asList(args);
    
    LOGGER.info("argList: " + argList);
    
    if (argList.contains("create")) {
      LOGGER.info("Creating clustered cache manager");
      final URI uri = create("terracotta://localhost:9510/clustered");
      try (CacheManager cacheManager = newCacheManagerBuilder()
              .with(cluster(uri).autoCreate().defaultServerResource("primary-server-resource"))
              .withCache("basicCache",
                      newCacheConfigurationBuilder(Long.class, String.class,
                              heap(100).offheap(1, MB).with(clusteredDedicated(5, MB))))
              .build(true)) {
        Thread.sleep(3000);
      }
    } else if (argList.contains("destroy")) {
      LOGGER.info("Destroying clustered cache manager");
      final URI uri = create("terracotta://localhost:9510/clustered");
      CacheManager cacheManager = newCacheManagerBuilder()
              .with(cluster(uri).autoCreate().defaultServerResource("primary-server-resource"))
              .withCache("basicCache",
                      newCacheConfigurationBuilder(Long.class, String.class,
                              heap(100).offheap(1, MB).with(clusteredDedicated(5, MB))))
              .build(true);
      Thread.sleep(3000);
      cacheManager.close();
      ((PersistentCacheManager)cacheManager).destroy();
    }
  }
}
