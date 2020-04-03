/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.start.site.extension.dependency.observability;

import java.util.List;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.metadata.DependencyGroup;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.MetadataElement;

/**
 * Add the actuator dependency if necessary if an observability library has been selected.
 *
 * @author Stephane Nicoll
 */
class ObservabilityBuildCustomizer implements BuildCustomizer<Build> {

	private final DependencyGroup observabilityGroup;

	ObservabilityBuildCustomizer(InitializrMetadata metadata) {
		this.observabilityGroup = metadata.getDependencies().getContent().stream()
				.filter((group) -> group.getName().equals("Observability")).findFirst().get();
	}

	@Override
	public void customize(Build build) {
		List<String> observabilityIds = this.observabilityGroup.getContent().stream().map(MetadataElement::getId)
				.collect(Collectors.toList());
		if (!build.dependencies().has("actuator") && build.dependencies().ids().anyMatch(observabilityIds::contains)) {
			build.dependencies().add("actuator");
		}
	}

}
