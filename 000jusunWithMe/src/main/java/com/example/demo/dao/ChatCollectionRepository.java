package com.example.demo.dao;
import org.bson.types.ObjectId;
import com.example.demo.vo.ChatCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ChatCollectionRepository extends MongoRepository<ChatCollection, ObjectId> {
	
	
}