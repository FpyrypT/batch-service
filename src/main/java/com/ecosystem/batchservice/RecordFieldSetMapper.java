package com.ecosystem.batchservice;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class RecordFieldSetMapper implements FieldSetMapper<Person> {

    public Person mapFieldSet(FieldSet fieldSet) {
        Person person = new Person();
        person.setId(fieldSet.readLong("id"));
        person.setName(fieldSet.readString("name"));
        person.setAge(fieldSet.readInt("age"));
        person.setPhone(fieldSet.readLong("phone"));
        return person;
    }
}
