package edu.gatech.CQLStorage.listener;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import edu.gatech.CQLStorage.entity.CQL;
import edu.gatech.CQLStorage.repo.CQLRepository;

@Component
public class StartupDeafultScriptInitializer{
	Logger log = LoggerFactory.getLogger(StartupDeafultScriptInitializer.class);
	PathMatchingResourcePatternResolver resolver;
	@Autowired
	CQLRepository repository;
	
	@EventListener(ApplicationReadyEvent.class)
	public void loadCQLScripts() {
		log.debug("Running loadCQLScripts.");
		resolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
		try {
			Resource[] resources = resolver.getResources("classpath*:/*.cql");
			for(Resource resource: resources) {
				log.debug("importing cql file:"+resource.getURL().toString());
				Scanner scanner = new Scanner(resource.getInputStream());
				scanner.useDelimiter("\\A");
				String body = scanner.hasNext() ? scanner.next() : "";
				scanner.close();
				String fileName = resource.getFilename();
				String name = fileName.substring(0, fileName.length()-4);//Lose the extension
				List<CQL> results = repository.findByName(name);
				CQL entity = results.isEmpty()? new CQL() : results.get(0);
				entity.setName(name);
				entity.setBody(body);
				log.debug("cql entity:"+entity.toString());
				CQL postEntity = repository.save(entity);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
