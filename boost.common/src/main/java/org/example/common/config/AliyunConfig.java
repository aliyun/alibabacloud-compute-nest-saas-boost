/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2.0

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

package org.example.common.config;

import com.aliyun.credentials.Client;
import com.aliyun.credentials.exception.CredentialException;
import com.aliyun.credentials.models.Config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;

@Data
@Configuration
public class AliyunConfig {

    private static final String CONFIG_TYPE = "ecs_ram_role";

    @Value("${ecs-role-name}")
    private String roleName;

    private Client client;

    public void createClient() throws CredentialException, ParseException, IOException {
        Config config = new Config();
        // 凭证类型。
        config.type = CONFIG_TYPE;
        config.roleName = roleName;
        this.client = new Client(config);
    }
}

