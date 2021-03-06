/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.cli;

import com.github.rvesse.airline.annotations.Command;
import io.netty.util.SuppressForbidden;
import org.apache.druid.initialization.DruidModule;
import org.apache.druid.initialization.Initialization;
import org.apache.druid.server.StatusResource;

@Command(
    name = "version",
    description = "Returns Druid version information"
)
public class Version implements Runnable
{
  @Override
  @SuppressForbidden(reason = "System#out")
  public void run()
  {
    System.out.println(new StatusResource.Status(Initialization.getLoadedImplementations(DruidModule.class)));
  }
}
