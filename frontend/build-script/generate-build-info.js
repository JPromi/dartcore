const { writeFileSync } = require('fs');
const { resolve } = require('path');


let functionReplaceList = {
    "buildDate": "new Date('" + new Date().toUTCString() + "')",
}

let buildInfo = {
    "version": process.env.npm_package_version || getPackageInfo().version,
    "buildDate": "%func(buildDate)%",
}

let tsContent = `export const buildInfo = ${JSON.stringify(buildInfo, null, 2)};`;

writeFileSync(resolve(__dirname, '../src/generated/build-info.ts'), replaceFunctions(tsContent));

function replaceFunctions(content) {
    for (let key in functionReplaceList) {
        content = content.replace(new RegExp(`"%func\\(${key}\\)%"`, 'g'), functionReplaceList[key]);
    }
    return content;
}

function getPackageInfo() {
    let packageInfo = {};
    try {
        packageInfo = require(resolve(__dirname, '../package.json'));
    } catch (error) {
        console.error('Error while reading package.json:', error);
    }
    return packageInfo;
}