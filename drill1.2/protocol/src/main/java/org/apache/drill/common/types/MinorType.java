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

package org.apache.drill.common.types;

public enum MinorType implements com.dyuproject.protostuff.EnumLite<MinorType>
{
    LATE(0),
    MAP(1),
    TINYINT(3),
    SMALLINT(4),
    INT(5),
    BIGINT(6),
    DECIMAL9(7),
    DECIMAL18(8),
    DECIMAL28SPARSE(9),
    DECIMAL38SPARSE(10),
    MONEY(11),
    DATE(12),
    TIME(13),
    TIMETZ(14),
    TIMESTAMPTZ(15),
    TIMESTAMP(16),
    INTERVAL(17),
    FLOAT4(18),
    FLOAT8(19),
    BIT(20),
    FIXEDCHAR(21),
    FIXED16CHAR(22),
    FIXEDBINARY(23),
    VARCHAR(24),
    VAR16CHAR(25),
    VARBINARY(26),
    UINT1(29),
    UINT2(30),
    UINT4(31),
    UINT8(32),
    DECIMAL28DENSE(33),
    DECIMAL38DENSE(34),
    NULL(37),
    INTERVALYEAR(38),
    INTERVALDAY(39),
    LIST(40),
    GENERIC_OBJECT(41);
    
    public final int number;
    
    private MinorType (int number)
    {
        this.number = number;
    }
    
    public int getNumber()
    {
        return number;
    }
    
    public static MinorType valueOf(int number)
    {
        switch(number) 
        {
            case 0: return LATE;
            case 1: return MAP;
            case 3: return TINYINT;
            case 4: return SMALLINT;
            case 5: return INT;
            case 6: return BIGINT;
            case 7: return DECIMAL9;
            case 8: return DECIMAL18;
            case 9: return DECIMAL28SPARSE;
            case 10: return DECIMAL38SPARSE;
            case 11: return MONEY;
            case 12: return DATE;
            case 13: return TIME;
            case 14: return TIMETZ;
            case 15: return TIMESTAMPTZ;
            case 16: return TIMESTAMP;
            case 17: return INTERVAL;
            case 18: return FLOAT4;
            case 19: return FLOAT8;
            case 20: return BIT;
            case 21: return FIXEDCHAR;
            case 22: return FIXED16CHAR;
            case 23: return FIXEDBINARY;
            case 24: return VARCHAR;
            case 25: return VAR16CHAR;
            case 26: return VARBINARY;
            case 29: return UINT1;
            case 30: return UINT2;
            case 31: return UINT4;
            case 32: return UINT8;
            case 33: return DECIMAL28DENSE;
            case 34: return DECIMAL38DENSE;
            case 37: return NULL;
            case 38: return INTERVALYEAR;
            case 39: return INTERVALDAY;
            case 40: return LIST;
            case 41: return GENERIC_OBJECT;
            default: return null;
        }
    }
}
