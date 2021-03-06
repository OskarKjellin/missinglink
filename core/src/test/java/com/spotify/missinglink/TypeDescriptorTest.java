/*
 * Copyright (c) 2015 Spotify AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.spotify.missinglink;

import com.google.common.collect.ImmutableMap;
import com.spotify.missinglink.datamodel.ArrayTypeDescriptor;
import com.spotify.missinglink.datamodel.ClassTypeDescriptor;
import com.spotify.missinglink.datamodel.PrimitiveTypeDescriptor;
import com.spotify.missinglink.datamodel.TypeDescriptor;
import com.spotify.missinglink.datamodel.TypeDescriptors;
import org.junit.Test;

import java.util.InputMismatchException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TypeDescriptorTest {

  @Test
  public void testEquality() {
    String[] signatures = {
            "B", "S", "I", "J", "F", "D", "Z", "C",
            "[D", "[[D", "[[[D",
            "LFoo;", "LBar;", "[LFoo;", "Lfoo/bar/Baz;"
    };
    for (String signature1 : signatures) {
      for (String signature2 : signatures) {
        if (signature1.equals(signature2)) {
          assertEquals(TypeDescriptors.fromRaw(signature1),
                       TypeDescriptors.fromRaw(signature2));
          assertEquals(TypeDescriptors.fromRaw(signature1).hashCode(),
                       TypeDescriptors.fromRaw(signature2).hashCode());
        } else {
          assertNotEquals(TypeDescriptors.fromRaw(signature1),
                          TypeDescriptors.fromRaw(signature2));
        }
      }
    }
  }

  @Test
  public void testDescriptions() {
    Map<String, String> desc = ImmutableMap.<String, String>builder()
        .put("B", "byte")
        .put("S", "short")
        .put("I", "int")
        .put("J", "long")
        .put("F", "float")
        .put("D", "double")
        .put("Z", "boolean")
        .put("C", "char")
        .put("[D", "double[]")
        .put("[[D", "double[][]")
        .put("[[[D", "double[][][]")
        .put("LFoo;", "Foo")
        .put("[LFoo;", "Foo[]")
        .put("[[LFoo;", "Foo[][]")
        .put("Lfoo/bar/Baz;", "foo.bar.Baz")
        .build();
    for (Map.Entry<String, String> entry : desc.entrySet()) {
      assertEquals(entry.getValue(), TypeDescriptors.fromRaw(entry.getKey()).toString());
    }
  }


  @Test
  public void testTypes() {
    Map<String, Class> desc = ImmutableMap.<String, Class>builder()
        .put("B", PrimitiveTypeDescriptor.class)
        .put("S", PrimitiveTypeDescriptor.class)
        .put("I", PrimitiveTypeDescriptor.class)
        .put("J", PrimitiveTypeDescriptor.class)
        .put("F", PrimitiveTypeDescriptor.class)
        .put("D", PrimitiveTypeDescriptor.class)
        .put("Z", PrimitiveTypeDescriptor.class)
        .put("C", PrimitiveTypeDescriptor.class)
        .put("[D", ArrayTypeDescriptor.class)
        .put("LFoo;", ClassTypeDescriptor.class)
        .put("[LFoo;", ArrayTypeDescriptor.class)
        .put("Lfoo/bar/Baz;", ClassTypeDescriptor.class)
        .build();
    for (Map.Entry<String, Class> entry : desc.entrySet()) {
      assertEquals(entry.getValue(), TypeDescriptors.fromRaw(entry.getKey()).getClass());
    }
  }

  @Test(expected = InputMismatchException.class)
  public void testInvalid() {
    TypeDescriptors.fromRaw("X");
  }

  @Test(expected = InputMismatchException.class)
  public void testMoarInvalid() {
    TypeDescriptors.fromRaw("LFoo");
  }

  @Test(expected = InputMismatchException.class)
  public void testMoastInvalid() {
    TypeDescriptors.fromClassName("LFoo;");
  }

  @Test(expected = InputMismatchException.class)
  public void testMoarDifferentInvalid() {
    TypeDescriptors.fromRaw("JJ");
  }

  @Test
  public void testCanonicalNames() throws Exception {
    final TypeDescriptor expected = TypeDescriptors.fromClassName("foo.Bar");
    final TypeDescriptor actual = TypeDescriptors.fromClassName("foo/Bar");
    assertEquals(expected, actual);
  }

  @Test
  public void testNewClassTypeDescriptor() throws Exception {
    final ClassTypeDescriptor a = TypeDescriptors.fromClassName("foo.Bar");
    final ClassTypeDescriptor b = TypeDescriptors.fromClassName("foo/Bar");
    assertEquals(a, b);
  }
}
