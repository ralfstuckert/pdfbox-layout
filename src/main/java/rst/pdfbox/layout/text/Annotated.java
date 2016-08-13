package rst.pdfbox.layout.text;

public interface Annotated extends Iterable<Annotation> {

    <T extends Annotation> Iterable<T> getAnnotationsOfType(Class<T> type);
}
