/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package test.unit.commands

import dev.kordex.gradle.plugins.docker.file.commands.RunCommand
import kotlin.test.Test

class RunCommandTest {
	@Test
	fun testBasicExec() {
		// RUN ["one", "two"]

		val command = RunCommand.Exec(
			arrayOf("one", "two")
		)

		val output = command.toString()

		assert(output.startsWith("RUN ")) {
			"Command output should start with `RUN `"
		}

		assert(output == "RUN [ \"one\", \"two\" ]") {
			"Command output should match in full"
		}
	}

	@Test
	fun testBasicShell() {
		// RUN one two

		val command = RunCommand.Shell("one two")
		val output = command.toString()

		assert(output.startsWith("RUN ")) {
			"Command output should start with `RUN `"
		}

		assert(output == "RUN one two") {
			"Command output should match in full"
		}
	}

	@Test
	fun testShellFormatting() {
		// RUN <<EOF
		// one
		// two
		// EOF

		val command = RunCommand.Shell("  one\ntwo  ")
		val output = command.toString()

		assert(
			output ==
				"RUN <<EOF\n" +
				"one\ntwo\n" +
				"EOF\n"
		) {
			"Command output should match in full"
		}
	}

	@Test
	fun testNetworkTypes() {
		var command: RunCommand

		// RUN --network=default
		//     one two

		command = RunCommand.Shell("one two")
		command.networkType(RunCommand.NetworkType.Default)

		assert(
			command.toString() ==
				"RUN --network=default " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should contain `default` network"
		}

		// RUN --network=host
		//     one two

		command = RunCommand.Shell("one two")
		command.networkType(RunCommand.NetworkType.Host)

		assert(
			command.toString() ==
				"RUN --network=host " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should contain `host` network"
		}

		// RUN --network=none
		//     one two

		command = RunCommand.Shell("one two")
		command.networkType(RunCommand.NetworkType.None)

		assert(
			command.toString() ==
				"RUN --network=none " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should contain `none` network"
		}
	}

	@Test
	fun testSecurityTypes() {
		var command: RunCommand

		// RUN --security=sandbox
		//     one two

		command = RunCommand.Shell("one two")
		command.securityType(RunCommand.SecurityType.Sandbox)

		assert(
			command.toString() ==
				"RUN --security=sandbox " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should contain `sandbox` security"
		}

		// RUN --security=insecure
		//     one two

		command = RunCommand.Shell("one two")
		command.securityType(RunCommand.SecurityType.Insecure)

		assert(
			command.toString() ==
				"RUN --security=insecure " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should contain `insecure` security"
		}
	}

	@Test
	fun testMultipleOptions() {
		// RUN --network=default
		//     --security=sandbox
		//     one two

		val command = RunCommand.Shell("one two")

		command.networkType(RunCommand.NetworkType.Default)
		command.securityType(RunCommand.SecurityType.Sandbox)

		assert(
			command.toString() ==
				"RUN --network=default " +
				"\\\n\t" +
				"--security=sandbox " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should contain `sandbox` security"
		}
	}

	@Test
	fun testMountBind() {
		// RUN --mount=type=bind,from=from,target=target,source=source,rw=true
		//     one two

		val command = RunCommand.Shell("one two")

		command.bindMount {
			from = "from"
			target = "target"

			readWrite = true
			source = "source"
		}

		assert(command.mount is RunCommand.MountBuilder.Bind) {
			"Command should contain a bind mount builder"
		}

		assert(
			command.toString() ==
				"RUN --mount=type=bind,from=from,target=target,source=source,rw=true " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should match in full"
		}

		command.bindMount {
			from = "from"
			target = "target"
		}

		assert(
			command.toString() ==
				"RUN --mount=type=bind,from=from,target=target,source=from " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should match in full"
		}
	}

	@Test
	fun testMountCache() {
		// RUN --mount=type=cache,id=id,target=target,sharing=private,from=from,source=source,mode=0777,uid=1000,gid=1000
		//     one two

		val command = RunCommand.Shell("one two")

		command.cacheMount {
			from = "from"
			target = "target"

			gid = 1000
			id = "id"
			mode = "0777"
			readOnly = false
			sharing = RunCommand.MountBuilder.Cache.Sharing.Private
			source = "source"
			uid = 1000
		}

		assert(command.mount is RunCommand.MountBuilder.Cache) {
			"Command should contain a cache mount builder"
		}

		assert(
			command.toString() ==
				"RUN --mount=type=cache,id=id,target=target,sharing=private,from=from,source=source,mode=0777,uid=1000,gid=1000 " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should match in full"
		}

		command.cacheMount {
			from = "from"
			target = "target"

			gid = 1000
			mode = "0777"
			readOnly = true
			sharing = RunCommand.MountBuilder.Cache.Sharing.Locked
			uid = 1000
		}

		assert(
			command.toString() ==
				"RUN --mount=type=cache,id=target,target=target,ro=true,sharing=locked,from=from,mode=0777,uid=1000,gid=1000 " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should match in full"
		}
	}

	@Test
	fun testMountTmpfs() {
		// RUN --mount=type=tmpfs,target=target,size=100M
		//     one two

		val command = RunCommand.Shell("one two")

		command.tmpfsMount {
			size = "100M"
			target = "target"
		}

		assert(command.mount is RunCommand.MountBuilder.TmpFS) {
			"Command should contain a tmpfs mount builder"
		}

		assert(
			command.toString() ==
				"RUN --mount=type=tmpfs,target=target,size=100M " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should match in full"
		}
	}

	@Test
	fun testMountSecret() {
		// RUN --mount=type=secret,id=id,target=target,required=true,mode=0420,uid=1000,gid=1000
		//     one two

		val command = RunCommand.Shell("one two")

		command.secretMount {
			gid = 1000
			id = "id"
			mode = "0420"
			required = true
			target = "target"
			uid = 1000
		}

		assert(command.mount is RunCommand.MountBuilder.Secret) {
			"Command should contain a secret mount builder"
		}

		assert(
			command.toString() ==
				"RUN --mount=type=secret,id=id,target=target,required=true,mode=0420,uid=1000,gid=1000 " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should match in full"
		}

		command.secretMount {
			gid = 1000
			mode = "0420"
			uid = 1000
		}

		assert(
			command.toString() ==
				"RUN --mount=type=secret,mode=0420,uid=1000,gid=1000 " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should match in full"
		}
	}

	@Test
	fun testMountSsh() {
		// RUN --mount=type=ssh,id=id,target=target,required=true,mode=0620,uid=1000,gid=1000
		//     one two

		val command = RunCommand.Shell("one two")

		command.sshMount {
			target = "target"

			gid = 1000
			id = "id"
			mode = "0620"
			required = true
			uid = 1000
		}

		assert(command.mount is RunCommand.MountBuilder.Ssh) {
			"Command should contain a ssh mount builder"
		}

		assert(
			command.toString() ==
				"RUN --mount=type=ssh,id=id,target=target,required=true,mode=0620,uid=1000,gid=1000 " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should match in full"
		}

		command.sshMount {
			target = "target"

			gid = 1000
			id = "id"
			mode = "0620"
			uid = 1000
		}

		assert(
			command.toString() ==
				"RUN --mount=type=ssh,id=id,target=target,mode=0620,uid=1000,gid=1000 " +
				"\\\n\t" +
				"one two"
		) {
			"Command output should match in full"
		}
	}

	@Test
	fun testFull() {
		// RUN --mount=type=cache,id=id,target=target,sharing=private,from=from,source=source,mode=0777,uid=1000,gid=1000
		//     --network=default
		//     --security=sandbox
		//     [ "one", "two" ]

		val command = RunCommand.Exec(arrayOf("one", "two"))

		command.networkType(RunCommand.NetworkType.Default)
		command.securityType(RunCommand.SecurityType.Sandbox)

		command.cacheMount {
			from = "from"
			target = "target"

			gid = 1000
			id = "id"
			mode = "0777"
			readOnly = false
			sharing = RunCommand.MountBuilder.Cache.Sharing.Private
			source = "source"
			uid = 1000
		}

		assert(command.mount is RunCommand.MountBuilder.Cache) {
			"Command should contain a cache mount builder"
		}

		assert(
			command.toString() ==
				"RUN --mount=type=cache,id=id,target=target,sharing=private,from=from,source=source,mode=0777,uid=1000,gid=1000 " +
				"\\\n\t" +
				"--network=default " +
				"\\\n\t" +
				"--security=sandbox " +
				"\\\n\t" +
				"[ \"one\", \"two\" ]"
		) {
			"Command output should match in full"
		}
	}
}
