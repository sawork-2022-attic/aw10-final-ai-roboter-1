/*
 * Copyright 2011-2022 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;

public class MicroposSimulation extends Simulation {

  // val headers_json = Map("Content-Type" -> "application/json")
  String cart = "{\"id\":0,\"items\":[{\"product\":{\"id\":84851,\"name\":\"Reversi Sensory Challenger\",\"price\":24.0,\"image\":\"https://images-na.ssl-images-amazon.com/images/I/31nTxlNh1OL.jpg\"},\"quantity\":110}]};";
  HttpProtocolBuilder httpProtocol =
      http
          // Here is the root for all relative URLs
          .baseUrl("http://localhost")
          // Here are the common headers
          .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
          .doNotTrackHeader("1")
          .acceptLanguageHeader("en-US,en;q=0.5")
          .acceptEncodingHeader("gzip, deflate")
          .userAgentHeader(
              "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0");

  // A scenario is a chain of requests and pauses
  ScenarioBuilder scn =
      scenario("Scenario Name")
          .exec(http("get products").get("/api/product"))
          .pause(Duration.ofMillis(200))
          .exec(http("add item 1").post("/api/item/84848").header("content-type", "application/json").body(StringBody("{\"quantity\": 10}")))
          .pause(Duration.ofMillis(150))
          .exec(http("add item 2").post("/api/item/84851").header("content-type", "application/json").body(StringBody("{\"quantity\": 20}")))
          .pause(Duration.ofMillis(150))
          .exec(http("get items").get("/api/item"))
          .pause(Duration.ofMillis(150))
          .exec(http("checkout").post("/api/item/toOrder").header("content-type", "application/json").body(StringBody(cart)))
          .pause(Duration.ofMillis(150))
          .exec(http("get delivery infomation").get("/api/delivery/1"))
          .pause(Duration.ofMillis(150));

  {
    setUp(scn.injectOpen(atOnceUsers(1000)).protocols(httpProtocol));
  }
}
