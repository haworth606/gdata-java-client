/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.google.gdata.wireformats.input;

import com.google.gdata.util.common.base.Preconditions;
import com.google.gdata.data.ParseSource;
import com.google.gdata.data.introspection.IServiceDocument;
import com.google.gdata.data.introspection.ServiceDocument;
import com.google.gdata.model.Element;
import com.google.gdata.util.ServiceException;
import com.google.gdata.wireformats.AltFormat;

import java.io.IOException;

/**
 * The AtomServiceDualParser class is an {@link InputParser} implementation
 * that is capable of parsing service documents into either the old or new
 * data model.
 * 
 * 
 */
public class AtomServiceDualParser 
    implements InputParser<IServiceDocument> {

  /**
   * Parser to use for old data model results
   */
  private final InputParser<ServiceDocument> dataParser = 
      new AtomServiceDataParser();

  /**
   * Parser to use for new data model results
   */
  private final InputParser<IServiceDocument> elementParser =
    ElementParser.of(AltFormat.ATOM_SERVICE, IServiceDocument.class);

  public AltFormat getAltFormat() {
    return AltFormat.ATOM_SERVICE;
  }

  public Class<IServiceDocument> getResultType() {
    return IServiceDocument.class;
  }

  public <R extends IServiceDocument> R parse(ParseSource parseSource,
      InputProperties inProps, Class<R> resultClass) throws IOException,
      ServiceException {
    Preconditions.checkNotNull(parseSource, "parseSource");
    Preconditions.checkNotNull(inProps, "inProps");
    Preconditions.checkNotNull("resultClass", resultClass);

    // Use the new data model parser for Element subtypes, otherwise the old one
    R result;
    if (Element.class.isAssignableFrom(resultClass)) {
      result = elementParser.parse(parseSource, inProps, resultClass);
    }
    if (ServiceDocument.class.isAssignableFrom(resultClass)) {
      @SuppressWarnings("unchecked")
      InputParser<R> castParser = (InputParser<R>) dataParser;
      result = castParser.parse(parseSource, inProps, resultClass);
    } else {
      throw new IllegalArgumentException("Invalid result type:" + resultClass);
    }
    return result;
  }
}
