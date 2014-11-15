package com.fan1tuan.test;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;
//import static org.springframework.data.mongodb.core.query��Query.query;


import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public class TestBean {
	private MongoTemplate template;

	public MongoTemplate getTemplate() {
		return template;
	}

	public void setTemplate(MongoTemplate template) {
		this.template = template;
	}
	
	public void insertDocument(String name, int age, String[] contents){
		Person temp = new Person(name, age);
		
		Character[] chs = new Character[contents.length];
		int i = 0;
		for(String str : contents){
			Character character = new Character();
			character.setContent(str);
			chs[i++] = character;
		}
		
		temp.setCharacters(chs);
		template.insert(temp);
	}
	
	public Person findOne(String name){  
		return template.findOne(new Query(where("name").is(name)), Person.class);
	}
	
	public List<Person> findAll(){
		return template.findAll(Person.class);
	}
	
	public void updateAgeByName(String name, int age){
		template.updateFirst(new Query(where("name").is(name)), update("age",age), Person.class);
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  void dropCollection(Class theClass){
		template.dropCollection(theClass);
	}
	
}
