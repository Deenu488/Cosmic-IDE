package org.cosmic.ide.activity.editor;

import android.content.res.AssetManager;
import androidx.annotation.WorkerThread;
import android.os.Bundle;
import com.tyron.kotlin_completion.util.PsiUtils;
import com.tyron.kotlin.completion.KotlinCompletionUtils;
import org.cosmic.ide.common.util.FileUtil;
import org.cosmic.ide.project.KotlinProject;
import org.cosmic.ide.project.Project;
import org.jetbrains.kotlin.psi.KtSimpleNameExpression;
import com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.eclipse.tm4e.core.registry.IGrammarSource;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.lang.completion.CompletionCancelledException;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandleResult;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.text.TextUtils;
import io.github.rosemoe.sora.util.MyCharacter;
import org.eclipse.tm4e.core.registry.IThemeSource;

import java.util.stream.Collectors;
import java.io.IOException;
import java.io.File;
import java.util.Collection;
import java.io.InputStreamReader;

public class KotlinLanguage extends TextMateLanguage {

    private final CodeEditor mEditor;
    private final KotlinProject mProject;
    private final File mCurrentFile;

    public KotlinLanguage(CodeEditor editor, Project project, File file, IThemeSource theme) throws IOException {
        super(
                IGrammarSource.fromInputStream(
                    editor.getContext().getAssets().open("textmate/kotlin/syntaxes/kotlin.tmLanguage"),
                    "kotlin.tmLanguage",
                    null
                ),
                new InputStreamReader(
                        editor.getContext().getAssets().open("textmate/kotlin/language-configuration.json")),
                theme,
                true);
        mEditor = editor;
        mCurrentFile = file;
        if (project instanceof KotlinProject) {
            mProject = (KotlinProject) project;
        } else {
            mProject = new KotlinProject(project.getRootFile());
        }
        setAutoCompleteEnabled(true);
    }

    @Override
    public int getInterruptionLevel() {
        return INTERRUPTION_LEVEL_STRONG;
    }

    @Override
    @WorkerThread
    public void requireAutoComplete(ContentReference content,
                                    CharPosition position,
                                    CompletionPublisher publisher,
                                    Bundle extraArguments) throws CompletionCancelledException {
        char c = content.charAt(position.getIndex() - 1);
//        if (!isAutoCompleteChar(c)) {
//            return;
//        }
        String prefix = CompletionHelper.computePrefix(content, position, this::isAutoCompleteChar);
        publisher.addItem(new SimpleCompletionItem(prefix.length(), "joe"));
        PsiElement psiElement = KotlinCompletionUtils.INSTANCE
                .getPsiElement(mCurrentFile, mProject, mEditor, mEditor.getCursor().getLeft());
        KtSimpleNameExpression parent =
                PsiUtils.findParent(psiElement, KtSimpleNameExpression.class);

        Collection<DeclarationDescriptor> referenceVariants = KotlinCompletionUtils.INSTANCE
                .getReferenceVariants(parent, name -> true, mCurrentFile, prefix);
        referenceVariants.stream().forEach(it -> {
            publisher.addItem(new SimpleCompletionItem(prefix.length(), it.getName().toString()));
        });
        try {
        FileUtil.writeFile(FileUtil.getDataDir() + "kotlin_completion.txt",
                String.join(", ", referenceVariants.stream().map(it -> it.getName().toString()).collect(Collectors.toList())));
        } catch (IOException e) {}
    }

    public boolean isAutoCompleteChar(char p1) {
        return true;
    }
}