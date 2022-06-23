/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2022  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package org.eclipse.tm4e.core.grammar;

/**
 * Result of the line tokenization API.
 *
 * @see <a href="https://github.com/Microsoft/vscode-textmate/blob/master/src/main.ts">https://github.com/Microsoft/vscode-textmate/blob/master/src/main.ts</a>
 *
 */
public interface ITokenizeLineResult {

    IToken[] getTokens();

    /**
     * Returns the `prevState` to be passed on to the next line tokenization.
     *
     * @return the `prevState` to be passed on to the next line tokenization.
     */
    StackElement getRuleStack();

}
