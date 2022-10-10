package ru.practicum.ExploreWithMe.mapper;

import ru.practicum.ExploreWithMe.model.Compilation;
import ru.practicum.ExploreWithMe.model.dto.CompilationDto;
import ru.practicum.ExploreWithMe.model.dto.NewCompilationDto;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return new Compilation(newCompilationDto.getPinned(), newCompilationDto.getTitle());
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(), compilation.isPinned(), compilation.getTitle());
    }
}
