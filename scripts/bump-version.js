import { exec, OutputMode } from 'https://deno.land/x/exec/mod.ts'
import { parseArgs } from 'https://deno.land/std@0.223.0/cli/parse_args.ts'

const flags = parseArgs(Deno.args)

const isFirstVersion = flags['first-version'] ?? false
const isAlphaVersion = flags?.alpha ?? false
const isBetaVersion = flags?.beta ?? false

let version

const output = (await exec('git cliff --unreleased --bump --context', { output: OutputMode.Capture })).output

if (output != null && output !== '') {
    const context = JSON.parse(output)
    version = context[0]?.version?.replace(/^([0-9.]+\.?[a-z]*\.?[0-9]*)/, 'v$1')
}

if (version == null && !isFirstVersion) {
    console.info('There is not a new version available')
    Deno.exit(0)
}

if (isFirstVersion && version == null) {
    version = 'v0.0.0'
} else if (isAlphaVersion) {
    version = version.replace(/^(v?[0-9.]+)$/, '$1-alpha.1')
} else if (isBetaVersion) {
    version = version.replace(/^(v?[0-9.]+)$/, '$1-beta.1')
} else {
    version = version.replace(/^(v[0-9.]+)(\-[a-z]+\.?[0-9]*)$/, '$1')
}

console.info(`The new version is ${version}`)

await exec(`git tag ${version}`)
await exec('git cliff --bump -o CHANGELOG.md')

function replaceVersion (file, searchRegex, newString) {
    const content = Deno.readTextFileSync(file)
    const newContent = content.replaceAll(searchRegex, newString)
    Deno.writeTextFileSync(file, newContent)
}

replaceVersion(
    'build.gradle.kts',
    /^version = \"v?[0-9.]+\.?[a-z]*\.?[0-9]*\"/gm,
    `version = "${version.replace(/^v/, '')}"`,
)

const files = [
    'CHANGELOG.md',
    'build.gradle.kts',
]

await exec(`git add ${files.join(' ')}`)
await exec(`git commit -m "🚀 bump version to ${version}"`)

console.info(`🚀 bumped to version ${version}`)
console.info('now you can run "git push"')
