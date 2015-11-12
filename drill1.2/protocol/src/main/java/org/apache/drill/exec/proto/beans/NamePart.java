/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Generated by http://code.google.com/p/protostuff/ ... DO NOT EDIT!
// Generated from protobuf

package org.apache.drill.exec.proto.beans;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.dyuproject.protostuff.GraphIOUtil;
import com.dyuproject.protostuff.Input;
import com.dyuproject.protostuff.Message;
import com.dyuproject.protostuff.Output;
import com.dyuproject.protostuff.Schema;

public final class NamePart implements Externalizable, Message<NamePart>, Schema<NamePart>
{
    public enum Type implements com.dyuproject.protostuff.EnumLite<Type>
    {
        NAME(0),
        ARRAY(1);
        
        public final int number;
        
        private Type (int number)
        {
            this.number = number;
        }
        
        public int getNumber()
        {
            return number;
        }
        
        public static Type valueOf(int number)
        {
            switch(number) 
            {
                case 0: return NAME;
                case 1: return ARRAY;
                default: return null;
            }
        }
    }


    public static Schema<NamePart> getSchema()
    {
        return DEFAULT_INSTANCE;
    }

    public static NamePart getDefaultInstance()
    {
        return DEFAULT_INSTANCE;
    }

    static final NamePart DEFAULT_INSTANCE = new NamePart();

    
    private Type type;
    private String name;
    private NamePart child;

    public NamePart()
    {
        
    }

    // getters and setters

    // type

    public Type getType()
    {
        return type == null ? Type.NAME : type;
    }

    public NamePart setType(Type type)
    {
        this.type = type;
        return this;
    }

    // name

    public String getName()
    {
        return name;
    }

    public NamePart setName(String name)
    {
        this.name = name;
        return this;
    }

    // child

    public NamePart getChild()
    {
        return child;
    }

    public NamePart setChild(NamePart child)
    {
        this.child = child;
        return this;
    }

    // java serialization

    public void readExternal(ObjectInput in) throws IOException
    {
        GraphIOUtil.mergeDelimitedFrom(in, this, this);
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        GraphIOUtil.writeDelimitedTo(out, this, this);
    }

    // message method

    public Schema<NamePart> cachedSchema()
    {
        return DEFAULT_INSTANCE;
    }

    // schema methods

    public NamePart newMessage()
    {
        return new NamePart();
    }

    public Class<NamePart> typeClass()
    {
        return NamePart.class;
    }

    public String messageName()
    {
        return NamePart.class.getSimpleName();
    }

    public String messageFullName()
    {
        return NamePart.class.getName();
    }

    public boolean isInitialized(NamePart message)
    {
        return true;
    }

    public void mergeFrom(Input input, NamePart message) throws IOException
    {
        for(int number = input.readFieldNumber(this);; number = input.readFieldNumber(this))
        {
            switch(number)
            {
                case 0:
                    return;
                case 1:
                    message.type = Type.valueOf(input.readEnum());
                    break;
                case 2:
                    message.name = input.readString();
                    break;
                case 3:
                    message.child = input.mergeObject(message.child, NamePart.getSchema());
                    break;

                default:
                    input.handleUnknownField(number, this);
            }   
        }
    }


    public void writeTo(Output output, NamePart message) throws IOException
    {
        if(message.type != null)
             output.writeEnum(1, message.type.number, false);

        if(message.name != null)
            output.writeString(2, message.name, false);

        if(message.child != null)
             output.writeObject(3, message.child, NamePart.getSchema(), false);

    }

    public String getFieldName(int number)
    {
        switch(number)
        {
            case 1: return "type";
            case 2: return "name";
            case 3: return "child";
            default: return null;
        }
    }

    public int getFieldNumber(String name)
    {
        final Integer number = __fieldMap.get(name);
        return number == null ? 0 : number.intValue();
    }

    private static final java.util.HashMap<String,Integer> __fieldMap = new java.util.HashMap<String,Integer>();
    static
    {
        __fieldMap.put("type", 1);
        __fieldMap.put("name", 2);
        __fieldMap.put("child", 3);
    }
    
}
