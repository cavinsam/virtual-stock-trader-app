#!/usr/bin/env node
const { spawn } = require('child_process');

// Collect args passed after `npm test -- ...`
let args = process.argv.slice(2) || [];

// Filter out Jest-specific flags that Vitest doesn't understand
args = args.filter(a => {
  // remove flags like --watchAll or --watchAll=false
  if (a.startsWith('--watchAll')) return false;
  return true;
});

// Ensure we run in non-watch mode in CI if no explicit mode is provided
if (!args.includes('--run') && !args.includes('--watch')) {
  args.unshift('--run');
}

// Spawn vitest via npx to ensure local binary resolution in CI
const cmd = process.platform === 'win32' ? 'npx.cmd' : 'npx';
const child = spawn(cmd, ['vitest', ...args], { stdio: 'inherit', shell: true });

child.on('exit', code => process.exit(code));
