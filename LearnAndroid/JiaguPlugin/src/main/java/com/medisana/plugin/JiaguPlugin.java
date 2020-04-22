package com.medisana.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.api.BaseVariantOutput;

import org.gradle.api.Action;
import org.gradle.api.DomainObjectCollection;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;

public class JiaguPlugin implements Plugin<Project> {

    //完成加固
    @Override
    public void apply(Project project) {
        //插件扩展: 使用者可以配置JiaguExt中的参数
        final JiaguExt jiaguExt = project.getExtensions().create("jiagu", JiaguExt.class);

        //注册监听
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(final Project project) {

                AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);

                appExtension.getApplicationVariants().all(new Action<ApplicationVariant>() {
                    @Override
                    public void execute(ApplicationVariant variant) {
                       //获取apk文件
                        variant.getOutputs().all(new Action<BaseVariantOutput>() {
                            @Override
                            public void execute(BaseVariantOutput baseVariantOutput) {
                                File outputFile = baseVariantOutput.getOutputFile();
                                String name = outputFile.getName();
                                //创建一个加固任务
                                project.getTasks().create("jiagu"+name,JiaguTask.class,outputFile,jiaguExt);
                            }
                        });

                    }
                });

            }
        });

    }
}
