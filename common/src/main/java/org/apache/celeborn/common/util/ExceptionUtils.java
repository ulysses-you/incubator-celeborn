/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.celeborn.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.celeborn.common.exception.CelebornIOException;
import org.apache.celeborn.common.exception.FileCorruptedException;
import org.apache.celeborn.common.exception.PartitionUnRetryAbleException;

public class ExceptionUtils {

  public static void wrapAndThrowIOException(Exception exception) throws IOException {
    if (exception instanceof CelebornIOException) {
      throw (CelebornIOException) exception;
    } else if (exception instanceof IOException) {
      throw new CelebornIOException(exception);
    } else {
      throw new CelebornIOException(exception.getMessage(), exception);
    }
  }

  public static Throwable wrapIOExceptionToUnRetryable(
      Throwable throwable, boolean convertAllIOException2UnRetryable) {
    if (throwable instanceof FileNotFoundException || throwable instanceof FileCorruptedException) {
      return new PartitionUnRetryAbleException(throwable.getMessage(), throwable);
    } else if (throwable instanceof IOException && convertAllIOException2UnRetryable) {
      return new PartitionUnRetryAbleException(throwable.getMessage(), throwable);
    } else {
      return throwable;
    }
  }

  public static String stringifyException(Throwable exception) {
    if (exception == null) {
      return "(null)";
    }

    try {
      StringWriter stm = new StringWriter();
      PrintWriter wrt = new PrintWriter(stm);
      exception.printStackTrace(wrt);
      wrt.close();
      return stm.toString();
    } catch (Throwable throwable) {
      return exception.getClass().getName() + " (error while printing stack trace)";
    }
  }
}
