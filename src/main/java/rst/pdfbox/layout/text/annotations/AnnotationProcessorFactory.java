package rst.pdfbox.layout.text.annotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Factory used to create all available {@link AnnotationProcessor}s. You may
 * {@link #register(Class) register} your own annotation processor in order to
 * process custom annotations.
 */
public class AnnotationProcessorFactory {

    private final static List<Class<? extends AnnotationProcessor>> ANNOTATION_PROCESSORS = new CopyOnWriteArrayList<Class<? extends AnnotationProcessor>>();

    static {
	register(HyperlinkAnnotationProcessor.class);
	register(UnderlineAnnotationProcessor.class);
    }

    /**
     * Use this method to register your (custom) annotation processors.
     * 
     * @param annotationProcessor
     *            the processor to register.
     */
    public static void register(
	    final Class<? extends AnnotationProcessor> annotationProcessor) {
	ANNOTATION_PROCESSORS.add(annotationProcessor);
    }

    /**
     * @return a (new) instance of all available annotation processors, both
     *         built-in and custom.
     */
    public static Iterable<AnnotationProcessor> createAnnotationProcessors() {
	List<AnnotationProcessor> annotationProcessors = new ArrayList<AnnotationProcessor>();
	for (Class<? extends AnnotationProcessor> annotationProcessorClass : ANNOTATION_PROCESSORS) {
	    try {
		annotationProcessors
			.add(annotationProcessorClass.newInstance());
	    } catch (Exception e) {
		throw new RuntimeException(
			"failed to create AnnotationProcessor", e);
	    }
	}
	return annotationProcessors;
    }
}
